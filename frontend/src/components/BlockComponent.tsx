import React, { useState } from 'react';
import { useSortable } from '@dnd-kit/sortable';
import { CSS } from '@dnd-kit/utilities';
import { ContentBlock, BlockType } from '@/types';
import { GripVertical, Trash2, Edit } from 'lucide-react';

interface BlockComponentProps {
  block: ContentBlock;
  isSelected: boolean;
  onSelect: () => void;
  onUpdate: (updates: Partial<ContentBlock>) => void;
  onDelete: () => void;
}

const BlockComponent: React.FC<BlockComponentProps> = ({
  block,
  isSelected,
  onSelect,
  onUpdate,
  onDelete,
}) => {
  const [isEditing, setIsEditing] = useState(false);

  const {
    attributes,
    listeners,
    setNodeRef,
    transform,
    transition,
    isDragging,
  } = useSortable({ id: block.id });

  const style = {
    transform: CSS.Transform.toString(transform),
    transition,
  };

  const renderBlockContent = () => {
    switch (block.type) {
      case BlockType.HEADING:
        const HeadingTag = `h${block.content.level || 2}` as keyof JSX.IntrinsicElements;
        return (
          <HeadingTag
            className={`font-bold ${
              block.content.level === 1
                ? 'text-4xl'
                : block.content.level === 2
                ? 'text-3xl'
                : block.content.level === 3
                ? 'text-2xl'
                : 'text-xl'
            }`}
          >
            {isEditing ? (
              <input
                type="text"
                value={block.content.text || ''}
                onChange={(e) =>
                  onUpdate({ content: { ...block.content, text: e.target.value } })
                }
                onBlur={() => setIsEditing(false)}
                className="w-full bg-transparent border-b-2 border-primary-500 focus:outline-none"
                autoFocus
              />
            ) : (
              block.content.text || 'Heading'
            )}
          </HeadingTag>
        );

      case BlockType.PARAGRAPH:
        return (
          <p className="text-base leading-relaxed">
            {isEditing ? (
              <textarea
                value={block.content.text || ''}
                onChange={(e) =>
                  onUpdate({ content: { ...block.content, text: e.target.value } })
                }
                onBlur={() => setIsEditing(false)}
                className="w-full min-h-[100px] bg-transparent border-2 border-primary-500 rounded p-2 focus:outline-none"
                autoFocus
              />
            ) : (
              block.content.text || 'Paragraph text...'
            )}
          </p>
        );

      case BlockType.IMAGE:
        return (
          <div className="bg-gray-100 rounded-lg flex items-center justify-center h-48">
            {block.content.src ? (
              <img
                src={block.content.src}
                alt={block.content.alt || ''}
                className="max-w-full max-h-full object-contain"
              />
            ) : (
              <span className="text-gray-400">Image placeholder</span>
            )}
          </div>
        );

      case BlockType.BUTTON:
        return (
          <button
            className={`px-6 py-3 rounded-lg font-medium ${
              block.content.variant === 'primary'
                ? 'bg-primary-600 text-white'
                : 'bg-gray-200 text-gray-900'
            }`}
          >
            {isEditing ? (
              <input
                type="text"
                value={block.content.text || ''}
                onChange={(e) =>
                  onUpdate({ content: { ...block.content, text: e.target.value } })
                }
                onBlur={() => setIsEditing(false)}
                className="bg-transparent border-b border-white focus:outline-none text-center"
                autoFocus
              />
            ) : (
              block.content.text || 'Button'
            )}
          </button>
        );

      case BlockType.LIST:
        const ListTag = block.content.ordered ? 'ol' : 'ul';
        return (
          <ListTag className={block.content.ordered ? 'list-decimal' : 'list-disc'}>
            {(block.content.items || []).map((item: string, index: number) => (
              <li key={index} className="ml-6">
                {item}
              </li>
            ))}
          </ListTag>
        );

      case BlockType.SPACER:
        return (
          <div
            style={{ height: block.content.height || '2rem' }}
            className="bg-gray-50 border-2 border-dashed border-gray-300 rounded flex items-center justify-center"
          >
            <span className="text-xs text-gray-400">Spacer ({block.content.height})</span>
          </div>
        );

      case BlockType.DIVIDER:
        return (
          <hr
            className="my-4"
            style={{
              borderColor: block.content.color || '#e5e7eb',
              borderStyle: block.content.style || 'solid',
            }}
          />
        );

      default:
        return <div>Unknown block type</div>;
    }
  };

  return (
    <div
      ref={setNodeRef}
      style={style}
      className={`relative group p-4 rounded-lg border-2 transition-all ${
        isSelected
          ? 'border-primary-500 bg-primary-50'
          : isDragging
          ? 'border-gray-300 opacity-50'
          : 'border-transparent hover:border-gray-300'
      }`}
      onClick={(e) => {
        e.stopPropagation();
        onSelect();
      }}
    >
      {/* Block Controls */}
      <div className="absolute top-2 right-2 flex items-center space-x-1 opacity-0 group-hover:opacity-100 transition-opacity bg-white rounded shadow-sm">
        <button
          {...attributes}
          {...listeners}
          className="p-1.5 text-gray-400 hover:text-gray-600 cursor-move"
        >
          <GripVertical className="w-4 h-4" />
        </button>
        <button
          onClick={(e) => {
            e.stopPropagation();
            setIsEditing(true);
          }}
          className="p-1.5 text-gray-400 hover:text-gray-600"
        >
          <Edit className="w-4 h-4" />
        </button>
        <button
          onClick={(e) => {
            e.stopPropagation();
            if (confirm('Delete this block?')) {
              onDelete();
            }
          }}
          className="p-1.5 text-red-400 hover:text-red-600"
        >
          <Trash2 className="w-4 h-4" />
        </button>
      </div>

      {/* Block Content */}
      <div>{renderBlockContent()}</div>
    </div>
  );
};

export default BlockComponent;

