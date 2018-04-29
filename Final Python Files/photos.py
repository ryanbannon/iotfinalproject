from firebase.firebase import FirebaseApplication
from picamera import PiCamera
from google.cloud import storage
from grovepi import *
from threading import Thread
import os
import time
import datetime

# Initialising sensors
button = 4
buzzer = 2
camera = PiCamera()

# Setting button and buzzers pin modes
pinMode(button,"INPUT")
pinMode(buzzer,"OUTPUT")

# Creating appropriate firebase object instances
firebase = FirebaseApplication('https://iotfinalproject-cea51.firebaseio.com/')
button_firebase = FirebaseApplication('https://iotfinalproject-cea51.firebaseio.com/app_button/')
storage_client = storage.Client.from_service_account_json('IOTFinalProject-13930536d2fc.json')
bucket_name = 'iotfinalproject-cea51.appspot.com'
bucket = storage_client.get_bucket(bucket_name)

# Method to generate the timestamp
def getTimestamp():
    return datetime.datetime.now().strftime("%H.%M.%S_%d-%b-%Y")

# Method that is called upon from the wii_scales.py file
def takePhotos():
    while True:
        try:
            time.sleep(5)

            # Getting value of the grovepi button
            button_status = digitalRead(button)

            # Getting and printing the value of the mobile apps button
            result = button_firebase.get('button',None)
            app_status = str(result)
            print (app_status)

            # if the button is pressed
            if button_status:

                # Thread methods for grovepi button  are below
                
                def listener(publisher):
                    global photo_button_state
                    photo_button_state = True
                    if not publisher.is_alive():
                        photo_button_thread = Thread(target=photo_button_publisher)
                    photo_button_thread.start()
                        
                def photo_button_publisher():
                    global photo_button_state

                    # Below defines the actions that are to be executed when this button has been pressed
                    while photo_button_state:
                        try:
                            timestamp = getTimestamp()
                            filename = str(timestamp+".jpg")
                            # Take the photo and name the file according to the filename stated in previous two lines
                            photo = camera.capture(filename)

                            blob = bucket.blob(os.path.basename(filename))
                            # Upload the file as a blob to firebase storage
                            blob.upload_from_filename(filename)
                            print('File {} uploaded to {}.'.format(filename, bucket))

                            # Now retrieve the public url of the updated file
                            photo_url = bucket.get_blob(filename)
                            print (blob)

                            # Assign that url to the the url variable below
                            url = photo_url.public_url
                            print (url)

                            # Post this url and the timestamp to firebase database
                            post = firebase.post('/photos/', {'timestamp':timestamp, 'URL':url})
                            print (post)
                            # Sound the grovepi buzzer for half a second to indicate successful upload
                            digitalWrite(buzzer,1)
                            time.sleep(0.5)
                            digitalWrite(buzzer,0)
                            time.sleep(0.5)
                            # Set the state to false to stop the thread
                            photo_button_state = False

                        except KeyboardInterrupt:
                            digitalWrite(buzzer,0)
                            break
                        except (IOError,TypeError) as e:
                            print ("Error")
                    print "Photo taken from the button"

                photo_button_thread = Thread(target=photo_button_publisher)
                photo_button_listener_thread = Thread(target=listener, args=(photo_button_thread,))

                # Start the thread                           
                photo_button_listener_thread.start()

            # if the mobile apps button is pressed
            if app_status == "True":

                # Thread methods for the mobile apps button are below

                def listener(publisher):
                    global photo_app_state
                    photo_app_state = True
                    if not publisher.is_alive():
                        photo_app_thread = Thread(target=photo_app_publisher)
                    photo_app_thread.start()

                def photo_app_publisher():
                    global photo_app_state

                    # Below defines the actions that are to be executed when this button has been pressed
                    while photo_app_state:
                        try:
                            timestamp = getTimestamp()
                            filename = str(timestamp+".jpg")
                            # Take the photo and name the file according to the filename stated in previous two lines
                            photo = camera.capture(filename)

                            blob = bucket.blob(os.path.basename(filename))
                            # Upload the file as a blob to firebase storage
                            blob.upload_from_filename(filename)
                            print('File {} uploaded to {}.'.format(filename, bucket))

                            # Now retrieve the public url of the updated file
                            photo_url = bucket.get_blob(filename)
                            print (blob)
                            
                            # Assign that url to the the url variable below
                            url = photo_url.public_url
                            print (url)

                            # Post this url and the timestamp to firebase database
                            post = firebase.post('/photos/', {'timestamp':timestamp, 'URL':url})
                            print (post)
                            # Sound the grovepi buzzer for half a second to indicate successful upload
                            digitalWrite(buzzer,1)
                            time.sleep(0.5)
                            digitalWrite(buzzer,0)
                            time.sleep(0.5)
                            # Set the state to false to stop the thread
                            photo_app_state = False
                            # Change the value of the button in firebase to ensure it does not get stuck in a constant loop
                            app_status = firebase.put('app_button','button','False')

                        except KeyboardInterrupt:
                            digitalWrite(buzzer,0)
                            break
                        except (IOError,TypeError) as e:
                            print ("Error")
                    print "Photo taken through the app"

                photo_app_thread = Thread(target=photo_app_publisher)
                photo_app_listener_thread = Thread(target=listener, args=(photo_app_thread,))

                # Start the thread                                   
                photo_app_listener_thread.start()

        except KeyboardInterrupt:
            digitalWrite(buzzer,0)
            break
        except (IOError,TypeError) as e:
            print ("Error")
