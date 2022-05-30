import imp
import Videogenerator
import DataSender 

dataSender = DataSender()
folder = './imagesout'

videoDataGenerator = Videogenerator(dataSender,"test","test","test",folder)
videoDataGenerator.generateData()
