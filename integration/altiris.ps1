param ($alert, $applicationInformation, $country, $hpomID, $ipAddress, $masterSiebelID, $messageGroup, $nodeName, $objectInformation, $severity, $submittedBy)

$url = 'https://altiris-sdk-qa.level3dc.net/CT.Create.Incident.Hpov/IncidentHPOV.asmx'
$headers = @{
    'Content-Type' = 'text/xml';
    'SOAPAction' = 'www.symantec.com/SubmitIncidentHPOV'
}

$envelope = @'
<?xml version="1.0" encoding="utf-8"?>
<soap:Envelope xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:xsd="http://www.w3.org/2001/XMLSchema" xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/">
  <soap:Body>
    <SubmitIncidentHPOV xmlns="www.symantec.com">
      <Alert>{0}</Alert>
      <ApplicationInformation>{1}</ApplicationInformation>
      <Country>{2}</Country>
      <HpomID>{3}</HpomID>
      <IPAddress>{4}</IPAddress>
      <MasterSiebelID>{5}</MasterSiebelID>
      <MessageGroup>{6}</MessageGroup>
      <NodeName>{7}</NodeName>
      <ObjectInformation>{8}</ObjectInformation>
      <Severity>{9}</Severity>
      <SubmittedBy>{10}</SubmittedBy>
    </SubmitIncidentHPOV>
  </soap:Body>
</soap:Envelope>
'@

$envelope = $envelope -f $alert, $applicationInformation, $country, $hpomID, $ipAddress, $masterSiebelID, $messageGroup, $nodeName, $objectInformation, $severity, $submittedBy

try
{
    # CALL
    $response = Invoke-WebRequest -Uri $url -Headers $headers -Method POST -Body $envelope

    # CONTENT   
    $statusCode = $response.StatusCode
    $workflowTrackingID = Select-Xml -Content $response.Content -XPath "//*[local-name()='WorkflowTrackingID']" | ForEach-Object {$_.node.InnerXML}
    $processID = Select-Xml -Content $response.Content -XPath "//*[local-name()='ProcessID']" | ForEach-Object {$_.node.InnerXML}
    $message = Select-Xml -Content $response.Content -XPath "//*[local-name()='Message']" | ForEach-Object {$_.node.InnerXML}

    # RETURN
    Write-Host ("Status code: ", $statusCode);
    Write-Host ("Message: ", $message);
    Write-Host ("Process ID: ", $processID);
    Write-Host ("Workflow tracking ID: ", $workflowTrackingID);
    if ($processID -like '*Error*') {
        exit 1
    }
} catch {
    # CONTENT ERROR
    $statusCode = $_.Exception.Response.StatusCode.value__
    $message = $_.Exception.Message

    # RETURN
    Write-Host ("Status code: ", $statusCode);
    Write-Host ("Message: ", $message);
    exit 1
}