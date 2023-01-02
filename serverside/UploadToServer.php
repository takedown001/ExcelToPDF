<?php
ini_set('display_startup_errors',1);
ini_set('display_errors',1);
include('config.php');
include 'vendor/autoload.php';
$file_path = "uploads/";
$KEY = 'uploaded_file';
ini_set('memory_limit', '2048M');
$file_path = $file_path . basename($_FILES[$KEY]['name']);
mysqli_query($connect,'TRUNCATE TABLE user');
if(move_uploaded_file($_FILES[$KEY]['tmp_name'], $file_path)) {
    
$file_name = $file_path;
            
              //$spreadsheet = \PhpOffice\PhpSpreadsheet\IOFactory::load($file_name);
            
            $file_type = \PhpOffice\PhpSpreadsheet\IOFactory::identify($file_name);
            $reader    = \PhpOffice\PhpSpreadsheet\IOFactory::createReader($file_type);
            $reader->setReadDataOnly(TRUE); $reader->setReadEmptyCells(FALSE);
            ini_set('memory_limit', '2048M');
            $spreadsheet = $reader->load($file_name);
            
            
            unlink($file_name);
            $worksheet = $spreadsheet->getActiveSheet();
            $n=1;
            foreach ($worksheet->getRowIterator() AS $row) {
                $cellIterator = $row->getCellIterator();
                $cellIterator->setIterateOnlyExistingCells(FALSE); // This loops through all cells,
                $rows = [];
                foreach ($cellIterator as $cell) {
                    $rows[] = $cell->getValue();
                }
                if($n==1)
                {
                
                }else{
                    $date= \PhpOffice\PhpSpreadsheet\Shared\Date::excelToDateTimeObject((int)$rows[2])->format('Y-m-d');
                    $time= \PhpOffice\PhpSpreadsheet\Shared\Date::excelToDateTimeObject((float)$rows[3])->format('H:i:s');
                    $insertqry="INSERT INTO `user`(`Calling_No`, `Called_No`, `Date`, `Time`, `Duration`, `Call_Type`, `IMEI`, `IMSI`, `First_Cell_ID`, `Second_Cell`) 
                        VALUES ('$rows[0]','$rows[1]','$date','$time','$rows[4]',
                        '$rows[5]','$rows[6]','$rows[7]','$rows[8]','$rows[9]')";
                       $insertres=mysqli_query($connect,$insertqry);
                }
                $n++;
            }
}
 ?>