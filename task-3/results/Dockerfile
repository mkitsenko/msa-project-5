# Стадия сборки
FROM python:3.9-alpine

ARG SOURCE_CONNECTION_STRING
ARG SQL_QUERY

ENV SOURCE_CONNECTION_STRING = ${SOURCE_CONNECTION_STRING}
ENV SQL_QUERY = ${SQL_QUERY}

USER root
WORKDIR /app
COPY ./requirements.txt ./

RUN pip install -r requirements.txt

COPY ./extractor.py ./

RUN adduser app --disabled-password --no-create-home --gecos ""

USER app
CMD ["python", "./extractor.py"]