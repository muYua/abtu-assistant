package com.mupei.assistant.dao;

import org.springframework.data.repository.CrudRepository;

import com.mupei.assistant.model.Message;

public interface MessageDao extends CrudRepository<Message, Long>{
	
}
