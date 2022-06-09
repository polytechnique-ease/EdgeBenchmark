FROM python:3.6-slim

RUN mkdir pythonserver
WORKDIR pythonserver

RUN apt-get update && apt-get install -y build-essential \
    cmake \
    wget \
    git \
    libgtk2.0-dev \
    && apt-get -y clean all \
    && rm -rf /var/lib/apt/lists/*
COPY requirements.txt /pythonserver/requirements.txt
RUN pip install -r requirements.txt

COPY / /pythonserver


ENTRYPOINT ["python3", "/pythonserver/MQTTAdapter.py"]
