FROM arm32v7/ubuntu
ENV TZ=America/Toronto

RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

RUN apt-get update && apt-get install -y default-jdk scala git wget software-properties-common
RUN add-apt-repository -y ppa:deadsnakes/ppa
RUN apt-get update
RUN apt-get install -y python3.8 python3-pip

COPY requirements.txt /requirements.txt
RUN pip install -r requirements.txt

COPY processing.py /processing.py
ENTRYPOINT python3 /processing.py && tail -f /dev/null