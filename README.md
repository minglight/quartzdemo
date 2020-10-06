Requirement

* A job(identify by jobId) can only run one instance at the same time
  * If a job has an running job id, it cannot be added/triggered.
* The failed job will have a retry mechanism 
  * The first retry will be delayed 5 seconds, second retry 10 sec...
  * Until reach MAX_RETRY
* The pending job will be executed after the server restarted
* The cluster instance will distribute the job execution randomly

Deployment

1. Install mysql
docker run --name quartzdb -e MYSQL_ROOT_PASSWORD=quartz -p 3306:3306 -itd  mysql:5.7

1. Run the QuartzDemoApplication with 3 different ports in application.yml

Test Steps

1. Call the api to test
curl -X POST 'http://localhost:8080/job?id=task1'


Test Scenario
1. Call with the same id to check duplicated id will be blocked
1. Call the url to check if the failed task will be retried
1. Restart all the instnces to check the tasks will be resumed 
 

