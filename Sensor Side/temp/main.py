from Videogenerator import VideoGenerator
from DataSender import DataSender

dataSender = DataSender()
folder = './imagesout'

videoDataGenerator = VideoGenerator(dataSender,"test",50,"10",folder)
videoDataGenerator.generateData()
