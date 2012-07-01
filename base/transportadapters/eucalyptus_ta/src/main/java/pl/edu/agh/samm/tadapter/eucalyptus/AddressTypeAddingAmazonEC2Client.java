package pl.edu.agh.samm.tadapter.eucalyptus;

import java.lang.reflect.Method;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.Request;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.services.ec2.AmazonEC2Client;
import com.amazonaws.services.ec2.model.RunInstancesRequest;
import com.amazonaws.services.ec2.model.RunInstancesResult;
import com.amazonaws.services.ec2.model.transform.RunInstancesRequestMarshaller;
import com.amazonaws.services.ec2.model.transform.RunInstancesResultStaxUnmarshaller;
import com.amazonaws.transform.Unmarshaller;

public class AddressTypeAddingAmazonEC2Client extends AmazonEC2Client {

	private static final Logger logger = LoggerFactory
			.getLogger(AddressTypeAddingAmazonEC2Client.class);

	public AddressTypeAddingAmazonEC2Client(AWSCredentials awsCredentials) {
		super(awsCredentials);
	}

	@Override
	public RunInstancesResult runInstances(
			RunInstancesRequest runInstancesRequest)
			throws AmazonServiceException, AmazonClientException {
		Request request = new RunInstancesRequestMarshaller()
				.marshall(runInstancesRequest);

		request.addParameter("AddressingType", "private");

		RunInstancesResult retVal = null;

		try {
			Method invokeMethod = AmazonEC2Client.class.getDeclaredMethod(
					"invoke", Request.class, Unmarshaller.class);
			invokeMethod.setAccessible(true);
			retVal = (RunInstancesResult) invokeMethod.invoke(this, request,
					new RunInstancesResultStaxUnmarshaller());
		} catch (Exception e) {
			logger.error("Problems invoking runInstances method!", e);
		}

		return retVal;
	}
}
