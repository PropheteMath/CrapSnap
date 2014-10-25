package crapsnap;

import net.rim.device.api.io.transport.ConnectionFactory;
import net.rim.device.api.io.transport.TransportInfo;

public class MyConnectionFactory extends ConnectionFactory{
	public MyConnectionFactory() 
	{	
		//Sets the preferred transport types and priority/order used 
                //  by the factory when attempting to get a connection:
                setPreferredTransportTypes( new int[] { 
				TransportInfo.TRANSPORT_TCP_WIFI, 
				TransportInfo.TRANSPORT_TCP_CELLULAR,
				TransportInfo.TRANSPORT_WAP2,
				TransportInfo.TRANSPORT_MDS,
	            TransportInfo.TRANSPORT_WAP,
		});

		//Sets the disallowed transport types used by the factory 
                //   attempting to get a connection:
		setDisallowedTransportTypes(new int[] {
               TransportInfo.TRANSPORT_BIS_B,
		});
		

		//Sets the maximum number of attempts the factory will make 
                //   to create a connection.  The default value is 1. 
                //   Valid values range from 1 to 500:
		setAttemptsLimit(10);
		
		//Sets connectionTimeout to the desired value (ms):
		setConnectionTimeout(5000);
		
		//Sets the maximum time (ms) the factory will try to 
                //   create a connection:
                setTimeLimit(5000);
	}
}
