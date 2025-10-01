package com.estonianport.unique.common.quartz

import org.quartz.spi.TriggerFiredBundle
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.quartz.SchedulerFactoryBean
import org.springframework.scheduling.quartz.SpringBeanJobFactory
import javax.sql.DataSource

@Configuration
class QuartzConfig(private val applicationContext: ApplicationContext) {

    @Bean
    fun springBeanJobFactory(): SpringBeanJobFactory {
        val jobFactory = object : SpringBeanJobFactory() {
            override fun createJobInstance(bundle: TriggerFiredBundle): Any {
                val job = super.createJobInstance(bundle)
                applicationContext.autowireCapableBeanFactory.autowireBean(job)
                return job
            }
        }
        return jobFactory
    }

    @Bean
    fun schedulerFactoryBean(jobFactory: SpringBeanJobFactory, dataSource: DataSource): SchedulerFactoryBean {
        val factory = SchedulerFactoryBean()
        factory.setJobFactory(jobFactory)
        factory.setDataSource(dataSource)
        // otros ajustes opcionales: factory.setOverwriteExistingJobs(true)
        return factory
    }
}
