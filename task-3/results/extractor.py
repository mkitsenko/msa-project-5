import os
import pandas as pd
from sqlalchemy import create_engine


def get_engine(connection_string: str):
    engine = create_engine(
        connection_string,
        pool_pre_ping=True,
        pool_size=int(os.environ.get("db_connection_pool_size", "5")),
        max_overflow=0,
    )
    return engine


def get_data(connection_string: str, select_query: str):
    engine = get_engine(connection_string)
    df = pd.read_sql(select_query, engine)
    return df


if __name__ == "__main__":
    conn_string = os.environ.get("SOURCE_CONNECTION_STRING")
    sql_query = os.environ.get("SQL_QUERY")

    print("RUNNING OF QUERY %s" % sql_query)
    data = get_data(connection_string=conn_string, select_query=sql_query)

    print(data.to_csv())
