import React from 'react';
import { useDraggable } from '@dnd-kit/core';
import { BlockType } from '@/types';
import {
  Heading,
  Type,
  Image as ImageIcon,
  MousePointerClick,
  List,
  Minus,
  SeparatorHorizontal,
} from 'lucide-react';

const blockTypes = [
  { type: BlockType.HEADING, icon: Heading, label: 'Heading' },
  { type: BlockType.PARAGRAPH, icon: Type, label: 'Paragraph' },
  { type: BlockType.IMAGE, icon: ImageIcon, label: 'Image' },
  { type: BlockType.BUTTON, icon: MousePointerClick, label: 'Button' },
  { type: BlockType.LIST, icon: List, label: 'List' },
  { type: BlockType.SPACER, icon: Minus, label: 'Spacer' },
  { type: BlockType.DIVIDER, icon: SeparatorHorizontal, label: 'Divider' },
];

const DraggableBlock: React.FC<{ type: BlockType; icon: any; label: string }> = ({
  type,
  icon: Icon,
  label,
}) => {
  const { attributes, listeners, setNodeRef, isDragging } = useDraggable({
    id: `palette-${type}`,
    data: { type },
  });

  return (
    <div
      ref={setNodeRef}
      {...attributes}
      {...listeners}
      className={`flex items-center space-x-3 p-3 bg-gray-50 hover:bg-gray-100 rounded-lg cursor-move transition-all ${
        isDragging ? 'opacity-50' : ''
      }`}
    >
      <Icon className="w-5 h-5 text-gray-600" />
      <span className="text-sm font-medium text-gray-900">{label}</span>
    </div>
  );
};

const BlockPalette: React.FC = () => {
  return (
    <div className="space-y-2">
      {blockTypes.map((block) => (
        <DraggableBlock
          key={block.type}
          type={block.type}
          icon={block.icon}
          label={block.label}
        />
      ))}
    </div>
  );
};

export default BlockPalette;

