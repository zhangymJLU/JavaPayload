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

package javapayload.stage;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.URL;
import java.util.Enumeration;

public class SystemInfo implements Stage {

	public void start(DataInputStream in, OutputStream out, String[] parameters) throws Exception {
		PrintStream pout = new PrintStream(out, true);
		pout.println("System properties:");
		pout.println("~~~~~~~~~~~~~~~~~~");		
		for (final Enumeration e = System.getProperties().propertyNames(); e.hasMoreElements(); ) {
			final String property = (String) e.nextElement();
			pout.println(property + "=" + System.getProperty(property));
		}
		pout.println();
		pout.println("Local address:");
		pout.println("~~~~~~~~~~~~~~");
		InetAddress addr = InetAddress.getLocalHost();
		pout.println("Name: "+addr.getHostName());
		/* #JDK1.4 */try {
		pout.println("Canonical Name: "+addr.getCanonicalHostName());
		} catch (NoSuchMethodError ex) /**/ {
			// no alternative available
		}
		pout.println("IP Address: "+addr.getHostAddress());
		pout.println();
		/* #JDK1.4 */try {
			final Enumeration e = java.net.NetworkInterface.getNetworkInterfaces();
			pout.println("Network interfaces:");
			pout.println("~~~~~~~~~~~~~~~~~~~");
			while(e.hasMoreElements()) {
				java.net.NetworkInterface iface = (java.net.NetworkInterface) e.nextElement();
				pout.println(iface.getName());
				pout.println("  Display Name: "+iface.getDisplayName());
				for (final Enumeration e2 = iface.getInetAddresses(); e2.hasMoreElements(); ) {
					InetAddress ifaddr = (InetAddress) e2.nextElement();
					pout.println("    Address:");
					pout.println("      Name: "+ifaddr.getHostName());
					pout.println("      Canonical Name: "+ifaddr.getCanonicalHostName());
					pout.println("      IP Address: "+ifaddr.getHostAddress());
				}
			}
			pout.println();
		} catch(NoClassDefFoundError ex) /**/ {
			// no alternative available
		}
		pout.println("External IP Address:");
		pout.println("~~~~~~~~~~~~~~~~~~~~");
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(new URL("http://www.ippages.com/simple/").openStream()));
			pout.println(br.readLine());
			br.close();
		} catch (Exception ex) {
			ex.printStackTrace(pout);
		}
		pout.println();
		pout.close();
	}
}
