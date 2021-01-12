<?php
  
 require 'functions.php';  
 require './lib/nusoap.php';

 $server=new nusoap_server();  
 $server->configureWSDL("ServerSoap","urn:ServerSoap");
 
$server->register(
	"Registration",
	array("login" => 'xsd:string', "password" => 'xsd:string', "mail" => 'xsd:string'),
	array("return"=>"xsd:boolean")
);
	
$server->register(
	"SignIn",
	array("login" => 'xsd:string', "password" => 'xsd:string', "getId" => 'xsd:boolean'),
	array("return"=>"xsd:integer")
);

$server->register(
	"AddBeer",
	array("name" => 'xsd:string', "type" => 'xsd:string', "brewery" => 'xsd:string', "percentAlcohol" => 'xsd:decimal', "container" => 'xsd:integer', "volume" => 'xsd:integer', "localisation" => 'xsd:string', "comment" => 'xsd:string', "score" => 'xsd:integer', "photo" => 'xsd:string', "date" => 'xsd:dateTime', "login" => 'xsd:string', "password" => 'xsd:string'),
	array("return"=>"xsd:boolean")
);
 
$server->register(
	"GetIdBeer",
	array("name" => 'xsd:string', "type" => 'xsd:string', "brewery" => 'xsd:string'),
	array("return"=>"xsd:integer")
);

$server->register(
	"BeerExists",
	array("name" => 'xsd:string', "type" => 'xsd:string', "brewery" => 'xsd:string', "percentAlcohol" => 'xsd:decimal', "container" => 'xsd:integer', "volume" => 'xsd:integer'),
	array("return"=>"xsd:integer")
);

$server->register(
	"AddCatalog",
	array("localisation" => 'xsd:string', "comment" => 'xsd:string', "score" => 'xsd:integer', "photo" => 'xsd:string', "date" => 'xsd:string', "fk_user" => 'xsd:integer', "fk_beer" => "xsd:integer"),
	array("return"=>"xsd:integer")
);

$server->register(
	"DeleteCatalog",
	array("idCatalog" => 'xsd:integer'),
	array("return"=>"xsd:boolean")
);

 $HTTP_RAW_POST_DATA = isset($HTTP_RAW_POST_DATA) ? $HTTP_RAW_POST_DATA : '';
 //$server->service($HTTP_RAW_POST_DATA);
 @$server->service(file_get_contents("php://input"));

 ?>