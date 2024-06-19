import csv
import numpy as np
from flask import Flask, request, jsonify, render_template, redirect, url_for
from tensorflow.keras.models import load_model
from tensorflow.keras.preprocessing.image import load_img,img_to_array
from werkzeug.utils import secure_filename
import tensorflow as tf
import os

app = Flask(__name__)

def get_name_data(index):
    ourClass={0: 'Alpukat',
                1: 'Anggur',
                2: 'Durian',
                3: 'Jambu', 
                4: 'Jeruk', 
                5: 'Langsat', 
                6: 'Mangga', 
                7: 'Manggis', 
                8: 'Naga', 
                9: 'Nanas', 
                10: 'Nangka', 
                11: 'Pepaya', 
                12: 'Pir', 
                13: 'Pisang', 
                14: 'Rambutan', 
                15: 'Salak', 
                16: 'Sawo', 
                17: 'Semangka', 
                18: 'Srikaya', 
                19: 'Stroberi'}
    return ourClass[index]


def normalization_image(current_path,size_normalization=(224,224)):
    """Normalization size of image to fit the required models needed
    input   : current path image
    Return  : expanded dimension image
    """
    image = load_img(current_path, target_size=(size_normalization))
    #change image to array
    x = img_to_array(image)
    #devided pixel of image into max 255
    x /= 255
    #expended dimension of images
    x = np.expand_dims(x, axis=0)
    return x

def load_mymodel(current_path,path_models):
    """
    Load model and make prediction 

    input: current path of image , path our models
    Returns: class prediction and probabilities 
    """
    #processing image
    expand_dimension=normalization_image(current_path)
    images = np.vstack([expand_dimension])
    #load models
    # if(os.path.exists(path_models)):
    #     model=load_model(path_models)
    # else :return None
    model=load_model(path_models)

    
    # prediction
    predict = model.predict(images)
    #find the max probabilities from the classes
    predicted_class_index = np.argmax(predict)
    #get the probalities 
    # predicted_class_prob = predict[0][predicted_class_index]
    print(predicted_class_index)
    return predicted_class_index



# end points
@app.route('/predict', methods=['POST'])
def predict():
    """
    Predict the disease of a guava leaf image uploaded by the user.

    Returns:
        Detail informantion about the fruits which hasbeen classification
    """
    # Check if an image file was uploaded
    if 'file' not in request.files:
        return jsonify({'error': 'No file found'})

    imagefile = request.files['file']

     # Check if the file is a valid image
    if imagefile.filename == '':
        return jsonify({'error': 'No image file selected'})

    if not imagefile.filename.lower().endswith(('.jpg', '.jpeg', '.png', '.gif')):
        return jsonify({'error': 'Invalid image file'})

    # Save the file
    file_path = secure_filename(imagefile.filename)
    # #save at the images predict
    imagefile.save(file_path)
    #please change the files right here to find out your model
    predict_class=load_mymodel(file_path,"../fruiteasyV5.h5")
    #check if the file does't exist
    if predict_class ==None:
        print("The model not found")
        return jsonify({"error" : "File Models is doesn't exits"}), 404
    #return only name and probabilites
    # return jsonify({'label':get_name_data(predict_class),"probablity":round(prob *100,2)})
    
    #remove files after get predict
    os.remove(file_path)
    #return directly request to other route
    return redirect(url_for('fruit_nutrition', buah=get_name_data(predict_class)))
     


@app.route('/nutrisi_buah')
def fruit_nutrition():
    # mendapatkan parameter 'buah' dari query string
    buah = request.args.get('buah')

    # mengecek apakah ada parameter yang dimasukkan 
    if not buah:
        return jsonify({'error' : 'Harap Masukkan Data Yang Sesuai'}), 400
    
    # open and read data-buah.csv
    with open('csv/data-buah.csv', 'r', newline='', encoding='utf-8') as databuah:
        reader = csv.DictReader(databuah)
        # Iterasi untuk mencari data berdasarkan nama_buah
        for row in reader:
            # jika buah ditemukan pada data-buah.csv maka return JSON
            if row['nama_buah'].lower() == buah.lower():
                return jsonify(row)
            
    # jika buah tidak ditemukan makan retirn error
    return jsonify({'error': 'Buah tidak ditemukan'}), 404

@app.route('/content_fruits')
def content_fruit_preparations():
    # mendapatkan parameter 'bulan' dari query string
    bulan = request.args.get('bulan')

    # mengecek apakah ada parameter yang dimasukkan 
    if not bulan:
        return jsonify({'error' : 'Harap Masukkan Data Yang Sesuai'}), 400
    #chcek if the params is int
    elif not(isinstance(bulan,str) and bulan.isdigit()):
        return jsonify({'error':'Parameter bulan harus bernilai angka positive'}),400
    # open and read content.csv
    with open('csv/content-fruits.csv', 'r', newline='', encoding='utf-8') as content_fruits:
        reader = csv.DictReader(content_fruits)
        #collected
        content=[]
        # Iterasi untuk mencari data content
        for row in reader:
            # jika buah ditemukan pada data-buah.csv maka return JSON
            if int(row['month']) == int(bulan):
                content.append(row)
        
        if(len(content) !=0):return jsonify(content)
    # jika buah tidak ditemukan makan retirn error
    return jsonify({'error': 'Buah tidak ditemukan'}), 404


@app.route("/musim_buah")
def musim_buah():
    "Endpoint to get fruits season and produce some field like  fruit,months,Icons"
    getBulan=request.args.get("bulan")
    # mengecek apakah ada parameter yang dimasukkan 
    if not getBulan:
        return jsonify({'error' : 'Harap Masukkan Data Yang Sesuai'}), 400
    #chcek if the params is int
    elif not(isinstance(getBulan,str) and getBulan.isdigit()):
        return jsonify({'error':'Parameter bulan harus bernilai angka positive'}),400
    # open and read content.csv
    with open('csv/season-content.csv', 'r', newline='', encoding='utf-8') as seasons:
        readerSeasons = csv.DictReader(seasons)
        seasonContent=[]
        for season in readerSeasons:
            # change months into array
            arrayBulan=string_to_array(season['bulan'])
            #chcek if getbulan contain at the season of months
            if(int(getBulan) in arrayBulan):
                seasonContent.append({"Buah":season['buah'],"Bulan":arrayBulan,"Icon":season['icon']})
        
        if(len(seasonContent) !=0):return jsonify(seasonContent)
    #condition for months is not found
    return jsonify({'error': 'Bulan tidak terdafatar saat ini'}), 404


def string_to_array(data,delimeter=";"):
    "to split string to array with delimter ;"
    if data.strip() == "":
        return []
    return [int(x.strip()) for x in data.split(delimeter)]
if __name__ == '__main__':
    app.run(host='0.0.0.0', port=3000, debug=True)
