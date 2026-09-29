class ProviderError(RuntimeError):
    pass


class ProviderUnavailable(
    ProviderError
):
    pass
