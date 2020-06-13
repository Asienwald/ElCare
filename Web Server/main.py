import flask
app = flask.Flask(__name__)

import json
import datetime

## User Account System ########################################
# config
app.config.update(
    DEBUG = True,
    SECRET_KEY = 'secret_xxx'
)

import requests
from flask_login import LoginManager, UserMixin, \
                                login_required, login_user, logout_user
# flask-login
login_manager = LoginManager()
login_manager.init_app(app)
login_manager.login_view = "login"

# silly user model
class User(UserMixin):
    def __init__(self,id):
        self.id = id
        self.name = "GodMode"
        self.password = "yeshucpo"
    def __repr__(self):
        return "%%s/%s" % (self.id, self.name, self.password)
    def match(self, name, password):
        return self.name == name and self.password == password

mainuser = User(0)

@login_manager.user_loader
def load_user(user_id):
    return User(user_id)

# somewhere to login
@app.route("/login", methods=["GET", "POST"])
def login():
    if flask.request.method == 'POST':
        username = flask.request.form['username']
        password = flask.request.form['password']        
        if mainuser.match(username, password):
            login_user(mainuser)
            return flask.redirect(flask.url_for('main'))
        else:
            return abort(401)
    else:
        return flask.render_template('login.html')


# somewhere to logout
@app.route("/logout")
@login_required
def logout():
    logout_user()
    return flask.redirect(flask.url_for('login'))

# handle login failed
@app.errorhandler(401)
def page_not_found(e):
    return flask.redirect(flask.url_for('login'))


## Auxilary ######################################################
from Sendgrid import *
def dateData():
    now = datetime.datetime.now()
    now.strftime("%Y-%m-%d %H:%M:%S")

    data = {
        "text":now.strftime("%Y-%m-%d %H:%M:%S"),
        "year":now.year,
        "month":now.month,
        "day":now.day,
        "hour":now.hour,
        "minute":now.minute,
        "second":now.second
        }
    return data

@app.route('/')
@login_required
def main():
    IOTView = {
        "sound" :"Detected" if hardwareData["sound"] == "1" else "Not Detected", 
        "motion" :"Detected" if hardwareData["sensor"] == "1" else "Not Detected", 
        "temperature" : str(hardwareData["temperature"])
        }
    return flask.render_template('dashboard.html',
                                 IOTView=IOTView,
                                 falls=fall,
                                 sounds=sounds)

@app.route('/monitoring')
@login_required
def monitor():
    return flask.render_template('monitoring.html')

## Falling #######################################################
fall = []
@app.route('/web/post/fall', methods=['POST'])
def fallen():
    data = dateData()
    fall.append(data)
    if len(fall)%5==0: notify("ELCare Fall Report","There have been 5 new cases of falls. Find out more at https://scdf-x-ibm-web.herokuapp.com/")
    return 'Done'

@app.route('/web/get/fall', methods=['GET'])
def getFallen():
    return json.dumps(fall)

## Sound ##############################################################
import IBMservice

sounds = []
@app.route('/web/post/sound', methods=['POST'])
def soundAnalysis():
    if flask.request.files:
        audio = flask.request.files['audio_data']
        audioData = audio.stream.read()
        textData = IBMservice.SpeechToText(audioData)
        toneData = IBMservice.ToneAnalyser(textData["text"])
        
        
        #print(flask.request.files)
    #print(flask.request)
    data = dateData()
    sounds.append({"spokenText":textData,"toneData":toneData, "dateData":data})
    if len(sounds)%5==0: notify("ELCare Sound Report","There have been 5 new cases of loud sounds. Find out more at https://scdf-x-ibm-web.herokuapp.com/")
    return 'hello'

@app.route('/web/get/sound', methods=['GET'])
def getSounds():
    return json.dumps(sounds)

## Hardware ######################################################
sensed = 0 # Number of sensor times
prevSensor = 1
hardwareData = {"sound": "1", "sensor": "0", "humidity": "33.00", "temperature": "27.00"}
@app.route('/hardware', methods=['GET','POST'])
def hardware():
    global hardwareData
    if flask.request.method == "POST":
        for key in flask.request.form:
            value = flask.request.form[key]
            hardwareData[key] = value
        print(f"Data: {flask.request.form} {hardwareData}")

        # Count sensed times
        '''
        if int(hardwareData["sensor"]) != prevSensor and prevSensor==0:sensed += 1
        prevSensor = int(hardwareData["sensor"])
        if sensed % 5 == 0:
            notify("ELCare Motion Report","There have been new cases of significant motion. Find out more at https://scdf-x-ibm-web.herokuapp.com/")           
            sensed += 1
        '''
        return 'Done'
    else:
        return json.dumps(hardwareData)

import os
### Running Code ##################################################
if __name__ == '__main__':
    try:
        port = os.environ.get('PORT')
        print(port)
    except Exception as e:
        print(e)
        port = 5000
    app.run(host="0.0.0.0", debug=True, port=port)#True)
