from flask import Flask
import lightControl
from flask_socketio import SocketIO
from flask_socketio import send, emit

app = Flask(__name__)
app.config['SECRET_KEY'] = 'secret!'
socketio = SocketIO(app, cors_allowed_origins="*")


@app.route('/set/<int:level>/<int:temp>/<string:point>')
def control_light(level, temp, point):
	id = 1
	if point == 'living':
		id = 0
	lightControl.control_light(level, temp, id)
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
	socketio.emit('update', {'frm': frm, 'to': to})

	if frm == 'living':
		control_light(0, 0, 0)
		switch_on_entrance()
		print(1)

	if frm == 'entrance':
		if to == 'kitchen':
			control_light(0, 0, 0)
		if to == 'living':
			control_light(100, 5000, 0)

		switch_off_entrance()
		print(2)

	if frm == 'kitchen':
		switch_on_entrance()
		print(3)

	return "OK"

@socketio.on('connect')
def test_connect():
	print('Connected')

@app.route('/send_socket/<int:id>/<int:level>/<int:temp>')
def send_socket(id, level, temp):
	socketio.emit('update', {'id': id, 'level': level, 'temp': temp })
	return "OK"

if __name__ == '__main__':
    socketio.run(app)