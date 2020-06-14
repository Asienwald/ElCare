FROM ubuntu:18.04

# set a directory for the app
WORKDIR /usr/src/app

RUN apt-get clean && mv /var/lib/apt/lists /tmp && mkdir -p /var/lib/apt/lists/partial && apt-get clean && apt-get update
RUN apt-get update
RUN apt-get install -y --no-install-recommends python3 python3-pip
RUN pip3 install flask flask_login requests
RUN pip3 install setuptools
RUN pip3 install sendgrid dnspython 
RUN pip3 install textblob

COPY . .

EXPOSE 5000 
CMD cd /usr/src/app && python3 main.py