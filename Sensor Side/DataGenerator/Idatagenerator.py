from abc import ABC, abstractmethod
from DataSender import DataSender

class Idatagenerator(ABC):

    datasender : DataSender
    @abstractmethod
    def generateData(self):
        pass
