from flask import Flask
import lightControl
from flask_socketio import SocketIO
from flask_socketio import send, emit
import datetime, time


app = Flask(__name__)
app.config['SECRET_KEY'] = 'secret!'
socketio = SocketIO(app, cors_allowed_origins="*")


@app.route('/set/<int:level>/<int:temp>/<string:point>')
def control_light(level, temp, point):
	# socketio.emit('update', {'id': point, 'level': level, 'temp': temp })
	id = 1
	if point == 'living':
		id = 0
	lightControl.control_light(level, temp, 0)
	return "OK"

@app.route('/blink')
def blink():
	lightControl.blink()
	return "OK"

@app.route('/switch_on_entrance')
def switch_on_entrance():
	lightControl.switch_on_entrance()
	return "OK"

@app.route('/switch_off_entrance')
def switch_off_entrance():
	lightControl.switch_off_entrance()
	return "OK"

@app.route('/move/<string:frm>/<string:to>')
def move(frm,to):
	socketio.emit('move', {'frm': frm, 'to': to})

	if frm == 'living':
		switch_on_entrance()
		time.sleep(1)
		control_light(0, 0, 0)

	if frm == 'entrance':
		if to == 'kitchen':
			control_light(0, 0, 0)
		if to == 'living':
			control_light(100, 5000, 0)
		time.sleep(1)
		switch_off_entrance()
		print(2)

	if frm == 'kitchen':
		switch_on_entrance()
		print(3)

	return "OK"

@socketio.on('connect')
def test_connect():
	print('Connected')

@app.route('/remote_control/<int:id>/<int:level>/<int:temp>')
def remote_control_socket(id, level, temp):
	socketio.emit('update', {'id': id, 'level': level, 'temp': temp })
	if id == 0:
		lightControl.control_light(level, temp, 0)
	return {"result": "OK"}

@socketio.on('update_light')
def update_light_socket(data):
	print(data)
	lightControl.control_light(data['level'], data['temp'], 0)

@socketio.on('blink')
def blink_socket(data):
	print(data)
	lightControl.blink()

@app.route('/remote_smart_demo')
def remote_smart_demo():
	socketio.emit('remote_smart_demo', {})
	return {"result": "OK"}

@app.route('/remote_enter')
def remote_enter():
	socketio.emit('move', {'frm': 'outside', 'to': 'entrance'})
	lightControl.switch_on_entrance()
	return "OK"


if __name__ == '__main__':
    socketio.run(app)