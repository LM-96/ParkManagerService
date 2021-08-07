var status_timer_itocc = document.getElementById("status_itocctimer");

const timeritocc_socket = new WebSocket(
    'ws://' +
    window.location.host +
    '/manager/timeritocc/'
);

timeritocc_socket.onmessage = function (e) {
    console.log(e)

    const json_data = JSON.parse(e.data);
    console.log(json_data)

    status_trolley.innerHTML = json_data.data
}

