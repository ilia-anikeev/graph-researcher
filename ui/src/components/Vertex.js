import React, { useRef, useState} from "react";
import Draggable from 'react-draggable';
import PropTypes from 'prop-types';
import './Vertex.css';

function Vertex(props) {
    const noderef = useRef();
    const inputRef = useRef(null);
    const [data, setData] = useState(props.index.toString()); 
    const [oldData, setOldData] = useState(props.index.toString());

    const handleDataChange = (e) => {
        const newData = e.target.value;
        props.addVertex(props.vertices.map(vertex =>{
            return (vertex.data === oldData) ? { ...vertex, data: newData } : vertex;
        }
        ));
        setData(newData);
        setOldData(newData);
        updateInputWidth();
    };


    const updateInputWidth = () => {
        if (inputRef.current) {
            const newWidth = data.length >= 6 ? data.length + 1 : 5;
            inputRef.current.style.width = `${newWidth}ch`;
        }
    };


    const setActualData = (e) => {
        updateInputWidth();
        setData(e.target.value)
    }


    return (
        <Draggable
            nodeRef={noderef}
            axis="both"
            defaultPosition={{ x: props.x - 25, y: props.y + 80}}
            position={props.isGraphArchiveMode || props.isUserGraphMode ? {x: props.x - 25, y:props.y + 80}  : null}
            grid={[1, 1]}
            scale={1}>
            <div className='vertex' ref={noderef}>
                <input
                    className='input'
                    type='text'
                    value={data} 
                    ref={inputRef}
                    onChange={setActualData}
                    onBlur={handleDataChange} 
                />
            </div>
        </Draggable>
    );
}

Vertex.propTypes = {
    index: PropTypes.number,
    vertices: PropTypes.array,
    addVertex: PropTypes.func,
    x: PropTypes.number,
    y: PropTypes.number,
    isGraphArchiveMode: PropTypes.bool,
    isUserGraphMode: PropTypes.bool
};

export default Vertex;