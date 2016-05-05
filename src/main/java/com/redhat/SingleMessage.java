package com.redhat;

import org.apache.activemq.ActiveMQSslConnectionFactory;

import javax.jms.Connection;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;

public class SingleMessage {

	public static void main(String[] args) throws Exception {
		ActiveMQSslConnectionFactory factory = new ActiveMQSslConnectionFactory();
		factory.setBrokerURL("failover://ssl://broker-beckham-amq-training.apps.latest.xpaas:443");

		factory.setUserName("tschloss");
		factory.setPassword("password");

		factory.setKeyStore("client.ks");
		factory.setKeyStorePassword("amq-demo");

		factory.setTrustStore("client.ts");
		factory.setTrustStorePassword("amq-demo");

		System.out.println("Creating connection...");
		Connection connection = factory.createConnection();
		try {
			connection.start();
			System.out.println("Creating session...");
			Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
			try {
				Queue q = session.createQueue("USERS.TEST.QUEUE");
				System.out.println("Creating producer...");
				MessageProducer producer = session.createProducer(q);
				try {
					producer.send(session.createTextMessage("Hello world!"));
					System.out.println("Message sent...");
				} finally {
					producer.close();
				}
			} finally {
				session.close();
			}
		} finally {
			connection.stop();
			connection.close();
		}
	}

}
