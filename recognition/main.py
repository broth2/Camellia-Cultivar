from flask import Flask, request
import pandas as pd
import torch
from torchvision import transforms, models
from PIL import Image
import urllib.request
from io import BytesIO

app = Flask(__name__)

device = torch.device("cuda" if torch.cuda.is_available() else "cpu")
labels= pd.read_json("./labels.json", typ='series')
all_labels=[]
for i in range(len(labels)):
    all_labels.append(labels[i+1])

test_transforms = transforms.Compose([transforms.Resize(255),
                                      transforms.CenterCrop(224),
                                      transforms.ToTensor(),
                                      transforms.Normalize([0.485, 0.456, 0.406],
                                                           [0.229, 0.224, 0.225])])
model = torch.load("./densenet_model.pth")


def predict(model, image):
    image = parseImage(image)
    results = {}
    optimizer.zero_grad()
    image = image.to(device)
    outputs = model(image)
    _, preds = torch.max(outputs.data, 1)
    for o in range(len(outputs[0])):
        results[o] = float(outputs[0][o])

    return results

def parseImage(img):
    image = Image.open(img)

    transform = transforms.Compose([transforms.Resize(255),
                                    transforms.CenterCrop(224),
                                    transforms.ToTensor(),
                                    transforms.Normalize([0.485, 0.456, 0.406],
                                    [0.229, 0.224, 0.225])])

    t = transform(img)

    return t.unsqueeze(0)

@app.route("/predict")
def classify():
    # guardar imagem quem vem no request no disco
    # e passar-lhe o path em Image.open() depois
    img = Image.open(path)
    return predict(model, img)


if __name__ == '__main__':
    app.run(host='0.0.0.0', debug=True)
