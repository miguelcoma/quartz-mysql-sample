package com.example.job;

import java.util.Optional;

import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobKey;
import org.quartz.SchedulerException;
import org.quartz.TriggerKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.example.model.Message;
import com.example.repository.MessageRepository;

public class MessageJob implements Job {

	private static final Logger log = LoggerFactory.getLogger(MessageJob.class);
	
	@Autowired
	private MessageRepository messageRepository;
	
	@Override
	public void execute (JobExecutionContext context) throws JobExecutionException {
	
		//Obtener el mensaje guardado 
		JobDataMap dataMap = context.getJobDetail().getJobDataMap();
		
		String messageId = dataMap.getString("messageId");
		
		log.info("Ejecutando el trabajo para el mensaje id {}", messageId);
		
		//Obtener mensaje de la base de datos por ID
		int id = Integer.parseInt(messageId);
		Optional<com.example.model.Message> messageOpt = messageRepository.findById(id);
		
		//Se gusrda y se cambia el mensaje de actualizacion a visible 
		Message message = messageOpt.get();
		message.setVisible(true);
        messageRepository.save(message);
		
		//anular o eliminar después de que se ejecute el trabajo
        try {
        	context.getScheduler().deleteJob(new JobKey(messageId));
        	TriggerKey triggerKey = new TriggerKey(messageId);
        	context.getScheduler().unscheduleJob(triggerKey);
        } catch (SchedulerException e){
        	e.printStackTrace();
        }
	}
	
	
}
