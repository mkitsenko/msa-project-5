
import pendulum
from airflow.decorators import dag
from airflow.providers.postgres.operators.postgres import PostgresOperator
from airflow.utils.trigger_rule import TriggerRule
from airflow.operators.python_operator import BranchPythonOperator, PythonOperator

def choose_branch(**kwargs):
    result = kwargs["ti"].xcom_pull("ingest_data")
    print(result)
    if len(result):
        return 'has_data_branch'
    else:
        return 'no_data_branch'
    
def print_result(**kwargs):
    print("Source " + kwargs['verb'] + " data!")

@dag(
    dag_id="msa-sprint-5-etl",
    default_args={
        "email": [
            "duty_user@super.org"
        ],
        "email_on_failure": True,
        "email_on_retry": False,
        "owner": "DRT",
        "retries": 3
    },
    description="Процесс оркестрации ETL для спринта №5",
    schedule=None,
    start_date=pendulum.datetime(2025, 9, 1, tz="UTC"),
    catchup=False,
    tags=["msa", "sprint5", "etl"],
    is_paused_upon_creation=True,
    render_template_as_native_obj=True,
    max_active_runs=1
)
def msa_sprint_5():

    ingest_data = PostgresOperator(
        task_id="ingest_data",
        trigger_rule=TriggerRule.NONE_FAILED,
        postgres_conn_id="local_db",
        sql="SELECT * FROM information_schema.tables",
        parameters={
        },
    )

    branch_operator = BranchPythonOperator(
        task_id="branch_operator",
        python_callable=choose_branch,
        provide_context=True,
    )

    has_data_operator = PythonOperator(
        task_id="has_data_branch",
        python_callable=print_result,
        op_kwargs={"verb": "has"}
    )

    no_data_operator = PythonOperator(
        task_id="no_data_branch",
        python_callable=print_result,
        op_kwargs={"verb": "has NO"}
    )

    ingest_data >> branch_operator >> [has_data_operator, no_data_operator]


msa_sprint_5()
