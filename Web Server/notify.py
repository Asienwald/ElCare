#!/usr/bin/env python

# using SendGrid's Python Library
# https://github.com/sendgrid/sendgrid-python
# Check https://app.sendgrid.com/guide/integrate/langs/python for API Key
import os
from API_KEYS import *
from sendgrid import SendGridAPIClient
from sendgrid.helpers.mail import Mail

apikey = SENDGRID_API_KEY
sg = SendGridAPIClient(apikey)#apiKeyos.environ.get('SENDGRID_API_KEY'))

def notify(subject,content,fromEmail="sd2green2011@gmail.com", toEmail="technologic190@gmail.com"):
    print("Notifying", fromEmail, "=>", toEmail)
    message = Mail(
        from_email=fromEmail,
        to_emails=toEmail,
        subject=subject,
        html_content=content)
    try:
        #sg = SendGridAPIClient(os.environ.get('SENDGRID_API_KEY'))
        print("Sending")
        response = sg.send(message)
        print("###Response###################################")
        print(response.status_code)
        print(response.body)
        print(response.headers)
    except Exception as e:
        print("Error:",e)

if __name__ == '__main__':
    #sms("helo")
    notify("hello","123")
    pass
