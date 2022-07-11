from enum import Enum, unique


@unique
class AttachmentType(str, Enum):
    IMAGE = "IMAGE"
    VIDEO = "VIDEO"
    AUDIO = "AUDIO"

    @staticmethod
    def from_str(label):
        if label == "IMAGE":
            return AttachmentType.IMAGE
        elif label == "VIDEO":
            return AttachmentType.VIDEO
        elif label == "AUDIO":
            return AttachmentType.AUDIO
        else:
            raise NotImplementedError
