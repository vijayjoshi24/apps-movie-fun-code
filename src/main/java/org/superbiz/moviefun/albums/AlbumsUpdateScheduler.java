package org.superbiz.moviefun.albums;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import javax.sql.DataSource;

@Configuration
@EnableAsync
@EnableScheduling
public class AlbumsUpdateScheduler {

    private static final long SECONDS = 1000;
    private static final long MINUTES = 60 * SECONDS;

    private final AlbumsUpdater albumsUpdater;

    private final DataSource dataSource;
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final JdbcTemplate jdbcTemplate;

    public AlbumsUpdateScheduler(AlbumsUpdater albumsUpdater,DataSource dataSource,JdbcTemplate jdbcTemplate) {
        this.albumsUpdater = albumsUpdater;
        this.dataSource = dataSource;
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    private static String CHECK_SQL_DB_FOR_JOB = "UPDATE album_scheduler_task SET started_at = now()";
    private static String SELECT_CHECK = " SELECT * from album_scheduler_task  WHERE started_at is NULL OR started_at < date_sub(NOW(),INTERVAL 2 MINUTE)";
    @Scheduled(initialDelay = 15 * SECONDS, fixedRate = 2 * MINUTES)
    public void run() {
        try {
            boolean isElgibileToStart = checkSchedulerToStart();
            if(isElgibileToStart) {
                logger.debug("Starting albums update");

                albumsUpdater.update();

                logger.debug("Finished albums update");
            }else{
                logger.debug("Nothing to start");
            }

        } catch (Throwable e) {
            logger.error("Error while updating albums", e);
        }
    }

    private boolean checkSchedulerToStart(){

        int selectSize = jdbcTemplate.queryForList(SELECT_CHECK).size();
        if(selectSize > 0) {
         jdbcTemplate.update(CHECK_SQL_DB_FOR_JOB);

            return true;
        }
        return false;
    }
}
