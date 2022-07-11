from dataclasses import dataclass
from typing import TypeVar, Generic

T = TypeVar('T')


@dataclass
class Error(Generic[T]):
    reason: T


@dataclass
class TextError(Error[str]):
    pass


@dataclass
class CodeTextError:
    code: int
    text: str
