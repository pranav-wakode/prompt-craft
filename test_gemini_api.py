#!/usr/bin/env python3

import requests
import json
import sys

# Read API key from local.properties
try:
    with open('local.properties', 'r') as f:
        content = f.read()
        for line in content.split('\n'):
            if line.startswith('GEMINI_API_KEY='):
                api_key = line.split('=', 1)[1]
                break
        else:
            print("API key not found in local.properties")
            sys.exit(1)
except FileNotFoundError:
    print("local.properties file not found")
    sys.exit(1)

# Test the API key with different model names
models_to_test = [
    "gemini-1.5-flash",
    "gemini-1.5-flash-latest", 
    "gemini-1.5-flash-001",
    "gemini-pro",
    "gemini-1.5-pro",
    "gemini-1.5-pro-latest"
]

def test_model(model_name, api_key):
    url = f"https://generativelanguage.googleapis.com/v1beta/models/{model_name}:generateContent?key={api_key}"
    
    payload = {
        "contents": [{
            "parts": [{
                "text": "Hello, this is a test."
            }]
        }]
    }
    
    headers = {
        'Content-Type': 'application/json',
    }
    
    try:
        response = requests.post(url, headers=headers, data=json.dumps(payload))
        return response.status_code, response.text
    except Exception as e:
        return None, str(e)

print("Testing Gemini API with different model names...")
print(f"API Key: {api_key[:20]}..." + "*" * (len(api_key) - 20))
print("-" * 60)

working_models = []
for model in models_to_test:
    print(f"Testing model: {model}")
    status_code, response_text = test_model(model, api_key)
    
    if status_code == 200:
        print(f"✅ {model} - WORKING")
        working_models.append(model)
    elif status_code == 404:
        print(f"❌ {model} - NOT FOUND (404)")
    elif status_code == 400:
        print(f"⚠️  {model} - BAD REQUEST (400)")
        if "API key" in response_text:
            print("   → Possible API key issue")
    else:
        print(f"❌ {model} - ERROR ({status_code}): {response_text[:100]}...")
    print("-" * 40)

print(f"\n🎯 Working models: {working_models}")
if working_models:
    print(f"✅ Recommended model to use: {working_models[0]}")
else:
    print("❌ No working models found. Please check your API key.")