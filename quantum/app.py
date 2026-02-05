"""Flask API for quantum portfolio optimization"""

from flask import Flask, jsonify, request
from flask_cors import CORS
from quantum_optimizer import get_quantum_recommendations

app = Flask(__name__)
CORS(app)


@app.route('/api/quantum/health', methods=['GET'])
def health_check():
    return jsonify({
        'status': 'healthy',
        'service': 'Quantum Portfolio Optimizer',
        'version': '1.0.0'
    })


@app.route('/api/quantum/recommend', methods=['GET'])
def get_recommendations():
    try:
        risk_profile = request.args.get('risk_profile', 'moderate')
        num_stocks = request.args.get('num_stocks', 5, type=int)
        valid_profiles = ['conservative', 'moderate', 'aggressive']
        if risk_profile not in valid_profiles:
            risk_profile = 'moderate'
        
        num_stocks = max(3, min(10, num_stocks))
        result = get_quantum_recommendations(risk_profile, num_stocks)
        
        return jsonify(result)
    
    except Exception as e:
        return jsonify({
            'success': False,
            'error': str(e)
        }), 500


@app.route('/api/quantum/profiles', methods=['GET'])
def get_risk_profiles():
    return jsonify({
        'profiles': [
            {
                'id': 'conservative',
                'name': 'Conservative',
                'description': 'Lower risk, stable returns. Ideal for risk-averse investors.',
                'icon': '🛡️'
            },
            {
                'id': 'moderate',
                'name': 'Moderate',
                'description': 'Balanced risk-reward. Suitable for most investors.',
                'icon': '⚖️'
            },
            {
                'id': 'aggressive',
                'name': 'Aggressive',
                'description': 'Higher risk, higher potential returns. For growth-focused investors.',
                'icon': '🚀'
            }
        ]
    })


if __name__ == '__main__':
    app.run(host='0.0.0.0', port=5000, debug=True)
