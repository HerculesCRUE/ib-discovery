FROM ubuntu:latest

RUN apt-get update && apt-get -y install cron

COPY ./scripts/backup-database /backup-database.sh

# Give execution rights on the cron job
RUN chmod 777 /backup-database.sh

# Copy hello-cron file to the cron.d directory
COPY ./scripts/cron-task /etc/cron.d/cron-task

# Give execution rights on the cron job
RUN chmod 0644 /etc/cron.d/cron-task

# Apply cron job
RUN crontab /etc/cron.d/cron-task

# Create the log file to be able to run tail
RUN touch /var/log/cron.log

# Run the command on container startup
CMD cron && tail -f /var/log/cron.log