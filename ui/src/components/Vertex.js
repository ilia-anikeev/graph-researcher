import React from "react"
import Draggable from 'react-draggable'
import PropTypes from 'prop-types';
import './Vertex.css'

function Vertex(props){
    const noderef = React.useRef();

    return (
            <Draggable nodeRef={noderef}
                axis="both"
                defaultPosition={{x: 200, y: 200}}
                position={null}
                grid={[1,1]}
                scale={1}
            >
                <div className='vertex' ref={noderef}>
                    <div className='text'>{props.data}</div>
                </div>
                
            </Draggable>
    )
}

Vertex.propTypes = {
    data: PropTypes.number
}

export default Vertex