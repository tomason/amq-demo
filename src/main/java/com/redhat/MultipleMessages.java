package com.redhat;

import org.apache.activemq.ActiveMQSslConnectionFactory;

import javax.jms.Connection;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;

public class MultipleMessages {
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
				Queue q = session.createQueue("USERS.MULTIPLE");
				System.out.println("Creating producer...");
				MessageProducer producer = session.createProducer(q);
				try {
					for (int i = 0; i < 20; i++) {
						producer.send(session.createTextMessage("Message #" + i));
					}
					System.out.println("Messages sent...");
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

		connection = factory.createConnection();
		try {
			connection.start();
			Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
			try {
				Queue q = session.createQueue("USERS.MULTIPLE");
				MessageConsumer consumer = session.createConsumer(q);
				try {
					while (true) {
						Message msg = consumer.receive(1_000L);
						if (msg != null) {
							String message;
							if (msg instanceof TextMessage) {
								message = "'" + ((TextMessage)msg).getText() + "'";
							} else {
								message = msg.toString();
							}
							System.out.println("Received message: " + message);
						} else {
							System.out.println("No more messages.");
							// no more messages
							break;
						}
					}
				} finally {
					consumer.close();
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
