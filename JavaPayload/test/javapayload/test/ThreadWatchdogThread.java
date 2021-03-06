/*
 * Java Payloads.
 * 
 * Copyright (c) 2010, 2011 Michael 'mihi' Schierl
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 
 * - Redistributions of source code must retain the above copyright notice,
 *   this list of conditions and the following disclaimer.
 *   
 * - Redistributions in binary form must reproduce the above copyright
 *   notice, this list of conditions and the following disclaimer in the
 *   documentation and/or other materials provided with the distribution.
 *   
 * - Neither name of the copyright holders nor the names of its
 *   contributors may be used to endorse or promote products derived from
 *   this software without specific prior written permission.
 *   
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND THE CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * HOLDERS OR THE CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,
 * BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS
 * OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR
 * TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE
 * USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package javapayload.test;

import java.util.Iterator;
import java.util.Map;

/**
 * A watchdog thread that watches if all threads terminate properly.
 */
public class ThreadWatchdogThread extends Thread {

	private final int delay;

	public ThreadWatchdogThread(int delay) {
		super("Thread Watchdog Thread");
		this.delay = delay;
		setDaemon(true);
	}

	public void run() {
		try {
			Thread.sleep(delay);
			Map stackTraces = (Map) Thread.class.getMethod("getAllStackTraces", new Class[0]).invoke(null, new Object[0]);
			for (Iterator it = stackTraces.keySet().iterator(); it.hasNext();) {
				Thread t = (Thread) it.next();
				if (!t.isDaemon()) {
					System.err.println("Thread " + t.getName() + " [" + t.getThreadGroup().getName() + "] still alive!");
					/* #JDK1.4 */try {
						StackTraceElement[] stackTrace = (StackTraceElement[]) stackTraces.get(t);
						for (int i = 0; i < stackTrace.length; i++) {
							System.err.println("\tat " + stackTrace[i]);
						}
					} catch (NoClassDefFoundError ex) /**/ {
						// no alternative available
					}
				}
			}
		} catch (Throwable t) {
			t.printStackTrace();
		}
		System.exit(5);
	};
}
