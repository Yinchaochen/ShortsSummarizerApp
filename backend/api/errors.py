"""
Centralised application error types.

Usage:
    raise AppError("USAGE_LIMIT", "Free limit reached.", status=403)

The response body will be:
    {"detail": {"code": "USAGE_LIMIT", "message": "Free limit reached."}}

Frontend can pattern-match on `detail.code` instead of brittle string comparisons.
"""

from fastapi import HTTPException


class AppError(HTTPException):
    """Structured application error with a machine-readable code field."""

    def __init__(self, code: str, message: str, status: int = 400):
        super().__init__(
            status_code=status,
            detail={"code": code, "message": message},
        )
