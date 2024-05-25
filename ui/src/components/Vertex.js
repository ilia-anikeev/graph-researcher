import React from "react";
import Draggable from 'react-draggable';
import PropTypes from 'prop-types';
import './Vertex.css';

function Vertex(props) {
    const noderef = React.useRef();
    const [data, setData] = React.useState(props.index); 

    const handleDataChange = (e) => {
        const newData = e.target.value;
        props.setVertex(props.vertices.map(vertex =>{
            console.log(vertex.data)
            return (vertex.data === data) ? { ...vertex, data: newData } : vertex
        }
        ));
        setData(newData);
    };

    return (
        <Draggable
            nodeRef={noderef}
            axis="both"
            defaultPosition={{ x: props.x, y: props.y }}
            position={null}
            grid={[1, 1]}
            scale={1}>
            <div className='vertex' ref={noderef}>
                <input
                    className='in'
                    type='text'
                    value={data} 
                    onChange={handleDataChange} 
                />
            </div>
        </Draggable>
    );
}

Vertex.propTypes = {
    index: PropTypes.string,
    vertices: PropTypes.array,
    setVertex: PropTypes.func,
    x: PropTypes.number,
    y: PropTypes.number,
};

export default Vertex;