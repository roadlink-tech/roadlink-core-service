## Remote Connection

This guide outlines the steps to enable and use the Execute Command (ECS Exec) in an Amazon ECS service, allowing remote
connections to running containers for debugging or management purposes.

### 1. Enable execute command

First, ensure the execute command is enabled on the ECS service. This enables the ability to execute commands
directly in the containers of the specified service.

```shell 
aws ecs update-service --cluster roadlink_cluster --service roadlink-core-service --enable-execute-command
```

Enables the ECS Exec feature on the specified ECS service, allowing commands to be run directly in the service's
containers.

### 2. Deploy a new task

To ensure new tasks start with the execute command capability enabled, force a new deployment of
the service.

```shell
aws ecs update-service --cluster roadlink_cluster --service roadlink-core-service --force-new-deployment
```

Forces a new deployment of the ECS service, ensuring that any new tasks started will have the ECS Exec feature enabled.

### 3. List available tasks

After forcing the deployment, list the current tasks to obtain their identifiers, which are needed to connect to a
container.

```shell
aws ecs list-tasks --cluster roadlink_cluster --service-name roadlink-core-service
```

### 4. Connect to a Container Using ECS Exec

Finally, use the execute command to start an interactive session inside a specific container of a task.

```shell
aws ecs execute-command \
  --region us-east-1 \
  --cluster roadlink_cluster \
  --task {{TASK_ID}} \
  --container roadlink-core-service \
  --command "/bin/sh" \
  --interactive

```