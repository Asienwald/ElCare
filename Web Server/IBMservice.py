# importing the requests library 
import requests 

import json

from API_KEYS import *
def SpeechToText(audioData, contentType='audio/wav'):
    print("Speech to Text:")
    headers = {'Content-type': contentType}
    r = requests.post(url=STT_API_ENDPOINT+"/v1/recognize",
                      auth=('apikey',STT_API_KEY),
                      headers=headers, data=audioData)
    #print(r.text)
    data = json.loads(r.text)
    try:
        text = data["results"][0]["alternatives"][0]["transcript"]
    except Exception as e:
        print(e)
        text = ""
    return {"text":text, "fullData":data}


def ToneAnalyser(text):
    print("Tone Analyser:")
    headers = {'Content-type': "application/json"}
    r = requests.post(url=TA_API_ENDPOINT+"/v3/tone?version=2017-09-21",
                      auth=('apikey',TA_API_KEY),
                      headers=headers,
                      data=json.dumps({"text":text}))
    #print(r.text)
    data = json.loads(r.text)
    try:
        toneVal = data["document_tone"]["tones"][0]["score"]
        toneName = data["document_tone"]["tones"][0]["tone_name"]
    except Exception as e:
        print("Tone analyser problem: ",e)
        toneVal = "-"
        toneName = "-"
    return {"fullData":data, "toneVal":toneVal, "toneName":toneName}
