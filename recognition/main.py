from flask import Flask, request, Response
import pandas as pd
import torch
from torchvision import transforms, models
from PIL import Image
import urllib.request
from io import BytesIO

app = Flask(__name__)

# allocate resources
device = torch.device("cuda" if torch.cuda.is_available() else "cpu")

# list of possible flowers
labels= pd.read_json("./labels.json", typ='series')
all_labels=[]
for i in range(len(labels)):
    all_labels.append(labels[i+1])

model = torch.load("./densenet_model.pth")


def predict(model, image):
    # number 96 is the camellia code
    image = parseImage(image)
    results = {}
    topResults = []
    image = image.to(device)
    outputs = model(image)
    _, preds = torch.max(outputs.data, 1)
    for o in range(len(outputs[0])):
        results[o] = float(outputs[0][o])
    
    for x in range(5):
        maxV = max(results, key=results.get)
        del results[maxV]
        topResults.append(maxV)

    if 96 in topResults:
        print("best case scenario")
        return Response("", status=201, mimetype='application/json')

    for y in range(10):
        maxV = max(results, key=results.get)
        del results[maxV]
        topResults.append(maxV)

    if 96 in topResults:
        print("acceptable case scenario")
        return Response("", status=200, mimetype='application/json')

    return Response("", status=406, mimetype='application/json')

def parseImage(img):
    # formats image to be compatible with the image samples

    transform = transforms.Compose([transforms.Resize(255),
                                    transforms.CenterCrop(224),
                                    transforms.ToTensor(),
                                    transforms.Normalize([0.485, 0.456, 0.406],
                                    [0.229, 0.224, 0.225])])

    t = transform(img)

    return t.unsqueeze(0)

@app.route("/predict")
def classify():
    path = request.args.get("url")
    mid = urllib.request.urlretrieve(path, "image.png")
    img = Image.open("image.png")
    print(path)
    return predict(model, img)


if __name__ == '__main__':
    app.run(host='0.0.0.0', debug=True)
