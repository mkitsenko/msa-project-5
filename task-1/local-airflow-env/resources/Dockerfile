# Стадия сборки
FROM apache/airflow:2.9.3
USER root
# Глобальные переменные стадии сборки
ENV AIRFLOW_IMAGE_NAME = 'apache/airflow:2.9.3'

# Установка бинарных пакетов Oracle драйверов
RUN apt update \
    && apt install -y gosu unixodbc unzip libgl1 libaio1 wget build-essential libc6-dev

COPY ./requirements.txt /tmp/

USER airflow
RUN pip install --no-cache-dir -r /tmp/requirements.txt

USER root
RUN apt-get clean -y && \
    apt-get autoclean -y && \
    apt-get remove -y wget unzip && \
    apt-get autoremove -y && \
    rm -rf /var/lib/apt/lists/* /var/lib/log/* /tmp/* /var/tmp/*