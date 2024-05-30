import csv
import numpy as np
from flask import Flask, request, jsonify, render_template, redirect, url_for
from tensorflow.keras.models import load_model
from tensorflow.keras.preprocessing.image import load_img,img_to_array
from werkzeug.utils import secure_filename
import os

app = Flask(__name__)

def get_name_data(index):
    ourClass={0: 'Avocado', 
              1: 'Banana',
              2: 'Dragonfruit',
              3: 'Durian', 
              4: 'Grape', 
              5: 'Guava', 
              6: 'Jackfruit', 
              7: 'Langsat', 
              8: 'Mango', 
              9: 'Mangostan', 
              10: 'Orange', 
              11: 'Papaya', 
              12: 'Pear', 
              13: 'Pineapple', 
              14: 'Rambutan', 
              15: 'Salak', 
              16: 'Sapodilla', 
              17: 'Soursop', 
              18: 'Strawberry', 
              19: 'Watermelon'}
    return ourClass[index]

def get_model(dir_models):
    """
    load the model
    input: path file 
    Return: load models
    """
    print(dir_models)
    #check if file available or not
    if(os.path.exists(dir_models)):
        return load_model(dir_models)
    else :return jsonify({"error" : "File Models is doesn't exits"}), 404

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
    model=get_model(path_models)
    #prediction
    predict = model.predict(images)
    #find the max probabilities from the classes
    predicted_class_index = np.argmax(predict)
    #get the probalities 
    predicted_class_prob = predict[0][predicted_class_index]
    return predicted_class_index, predicted_class_prob



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
    imagefile.save(file_path)
    predict_class,prob=load_mymodel(file_path,"model/fruits_model.h5")
    
    #return only name and probabilites
    # return jsonify({'label':get_name_data(predict_class),"probablity":round(prob *100,2)})
    
    #return directly request to other route
    return redirect(url_for('fruit_nutrition', fruit=get_name_data(predict_class)))
     


@app.route('/nutrisi_buah')
def fruit_nutrition():
    # mendapatkan parameter 'buah' dari query string
    buah = request.args.get('fruit')

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

if __name__ == '__main__':
    app.run(host='0.0.0.0', port=3000, debug=True)