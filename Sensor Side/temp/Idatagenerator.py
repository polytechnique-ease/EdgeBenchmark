from abc import ABC, abstractmethod
import DataSender

class Idatagenerator(ABC):

    datasender : DataSender
    @abstractmethod
    def generateData(self):
        pass
