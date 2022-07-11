from enum import Enum, unique


@unique
class EventType(str, Enum):
    OFFLINE = "OFFLINE"
    ONLINE = "ONLINE"

    @staticmethod
    def from_str(label):
        if label == "OFFLINE":
            return EventType.OFFLINE
        elif label == "ONLINE":
            return EventType.ONLINE
        else:
            return None
