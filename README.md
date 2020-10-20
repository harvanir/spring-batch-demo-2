# Spring Batch Demo

## Run docker mysql independently
```shell script
./run-mysql
```

## Build SpringBoot application
```shell script
./build
```

## Run docker SpringBoot application independently
We will create dummy data:
  - 100,000 rows of Item's data
  - 10,000 rows of Order's data
  - 10,000 * 10 rows of Order Item's data
```shell script
./run-spring-boot
```  

## Generate Report ##
- Available report {id}:
  - 1 (Items)
  - 2 (Orders)
- Reading from database mode {usePaginate}:
  - true
  - false
- Available type {fileType}:
  - csv
  - excel
- Generate report by invoking REST API:
  - [GET] http://localhost:8888/generate/{id}?usePaginate={usePaginate}&fileType={fileType}
- Output directory: <code>docker-compose/spring-boot/dir/output</code>