import flask
app = flask.Flask(__name__)

import json
import datetime

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
def main():
    return flask.render_template('monitoring.html')

## Falling #######################################################
fall = []
@app.route('/web/post/fall', methods=['POST'])
def fallen():
    data = dateData()
    fall.append(data)
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
        
        #print(flask.request.files)
    #print(flask.request)
    data = dateData()
    sounds.append({"spokenText":textData, "dateData":data})
    return 'hello'

@app.route('/web/get/sound', methods=['GET'])
def getSounds():
    return json.dumps(sounds)

## Hardware ######################################################=
hardwareData = {"sound": "1", "sensor": "1", "humidity": "33.00", "temperature": "27.00"}
@app.route('/hardware', methods=['GET','POST'])
def hardware():
    global hardwareData
    if flask.request.method == "POST":
        for key in flask.request.form:
            value = flask.request.form[key]
            hardwareData[key] = value
        print(f"Data: {flask.request.form} {hardwareData}")
        return 'Done'
    else:
        return json.dumps(hardwareData)

### Running Code ##################################################
if __name__ == '__main__':
    app.run(host="0.0.0.0", port=5000, debug=True)
