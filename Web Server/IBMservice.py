# importing the requests library 
import requests 

import json

from IBM_API_KEYS import *
def SpeechToText(audioData, contentType='audio/wav'):
    print("Speech to Text:")
    headers = {'Content-type': contentType}
    r = requests.post(url=STT_API_ENDPOINT+"/v1/recognize", auth=('apikey',STT_API_KEY), headers=headers, data=audioData)
    print(r.text)
    data = json.loads(r.text)
    try:
        text = data["results"][0]["alternatives"][0]["transcript"]
    except Exception as e:
        print(e)
        text = ""
    return {"text":text, "fullData":data}

