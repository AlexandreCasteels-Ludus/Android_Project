<?php

function ConnectionDataBase(){

    $serv = "localhost";
    $user = "root";
    $pass = "";
    $db = "beerapp"; // pour sqli
    $dsn = "mysql:dbname=beerapp;host=127.0.0.1:3306"; // pour pdo

    $conn = new PDO($dsn,$user,$pass) or die("Connection Failed");

    return $conn;
}

function DataSanitization($data){
    return htmlspecialchars($data);
}
//-----------------------USER MANAGEMENT--------------------------

function Registration($login, $password, $mail){

    $conn = ConnectionDataBase();

    if(! SignIn($login, $password, $getId = false))
    {
        $login = DataSanitization($login);
        $password = DataSanitization($password);
        $mail = DataSanitization($mail);

        $hashpw = hash("sha256", $password);

        $query ='insert into user (name, password, mail) values ("' . $login . '","' . $hashpw . '","' . $mail . '")';
        
        return $conn->query($query);
    }

    return false;
}


function SignIn($login, $password, $getId){

    $conn = ConnectionDataBase();

    $login = DataSanitization($login);
    $password = DataSanitization($password);

    $hashpw = hash("sha256", $password);

    $query='select _id from user where name="' . $login . '" AND password="' . $hashpw . '"';
        
    $res  = $conn->query($query);
    $data = $res->fetch(PDO::FETCH_ASSOC);

    if($getId)
        return $data;

    return !empty($data);
}


//-------------------------Beer-----------------------


function AddBeer($name, $type, $brewery, $percentAlcohol, $container, $volume, $localisation, $comment, $score, $photo, $date, $login, $password)
{
    $conn = ConnectionDataBase();

    if(! BeerExists($name, $type, $brewery, $percentAlcohol, $container, $volume))
    {
        $name = DataSanitization($name);
        $type = DataSanitization($type);
        $brewery = DataSanitization($brewery);
        $percentAlcohol = DataSanitization($percentAlcohol);
        $container = DataSanitization($container);
        $volume = DataSanitization($volume);
        $localisation = DataSanitization($localisation);
        $comment = DataSanitization($comment);
        $score = DataSanitization($score);
        $photo = DataSanitization($photo);
        $date = DataSanitization($date);

        $query ='insert into beer (name, type, brewery, percentAlcohol, container, volume) values ("' . $name . '", "' . $type . '", "' . $brewery . '", "' . $percentAlcohol . '", "' . $container . '", "' . $volume .'"';
        
        if($conn->query($query) == false)
            return false;
        else{
            $fk_user = SignIn($login, $password, $getId = true);
            $fk_beer = GetIdBeer($name, $type, $brewery);

            return AddCatalog($localisation, $comment, $score, $photo, $date, $fk_user, $fk_beer);
        }
    }
    else{
        $fk_user = SignIn($login, $password, $getId = true);
        $fk_beer = GetIdBeer($name, $type, $brewery);
        
        return AddCatalog($localisation, $comment, $score, $photo, $date, $fk_user, $fk_beer);
    }
    return false;   
}


function GetIdBeer($name, $type, $brewery){

    $conn = ConnectionDataBase();

    $name = DataSanitization($name);
    $type = DataSanitization($type);
    $brewery = DataSanitization($brewery);

    $query='select _id from beer where name="' . $name . '" AND type="' . $type . '" AND brewery="' . $brewery . '"';
        
    $res  = $conn->query($query);
    $data = $res->fetch(PDO::FETCH_ASSOC);

    if(empty($data))
        return -1;

    return array_values($data)[0];
}


function BeerExists($name, $type, $brewery, $percentAlcohol, $container, $volume){

    $conn = ConnectionDataBase();

    $name = DataSanitization($name);
    $type = DataSanitization($type);
    $brewery = DataSanitization($brewery);
    $percentAlcohol = DataSanitization($percentAlcohol);
    $container = DataSanitization($container);
    $volume = DataSanitization($volume);    

    $query='select _id from beer where name="' . $name . '" AND type="' . $type . '" AND brewery="' . $brewery . '" AND percentAlcohol="' . $percentAlcohol . '" AND container="' . $container . '" AND volume="' . $volume . '"';
        
    $res  = $conn->query($query);
    $data = $res->fetch(PDO::FETCH_ASSOC);
    
    return empty($data);
}


//----------------Catalog---------------------

function AddCatalog($localisation, $comment, $score, $photo, $date, $fk_user, $fk_beer){
    $conn = new PDO($dsn,$user,$pass) or die("Connection Failed");

    $localisation = DataSanitization($localisation);
    $comment = DataSanitization($comment);
    $score = DataSanitization($score);
    $photo = DataSanitization($photo);
    $date = DataSanitization($date);
        
    $query = 'insert into catalog (localisation, comment, score, photo, date, fk_user, fk_beer) values ("' . $localisation . '","' . $comment . '","' . $score . '","'  . $photo . '","'  . $date . '","'  . $fk_user . '","'  . $fk_beer . '"';

    return $conn->query($query);
}

function DeleteCatalog($idCatalog){
    
    $conn = ConnectionDataBase();

    $query = 'delete from catalog where _id ="' . $idCatalog . '"';

    return $conn->query($query);
}


?>