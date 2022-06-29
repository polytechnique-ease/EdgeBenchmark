FROM python:3.6-slim

RUN mkdir camera
WORKDIR camera
RUN mkdir imagesout

RUN apt-get update && apt-get install -y build-essential \
    cmake \
    wget \
    git \
    libgtk2.0-dev \
    && apt-get -y clean all \
    && rm -rf /var/lib/apt/lists/*
COPY /requirements.txt /camera/requirements.txt
RUN pip install -r requirements.txt

COPY / /camera


ENTRYPOINT ["python3", "/camera/main.py"]
