import imp
import Videogenerator
import DataSender 

dataSender = DataSender()
folder = './imagesout'

videoDataGenerator = Videogenerator(dataSender,"test","50","test",folder)
videoDataGenerator.generateData()
