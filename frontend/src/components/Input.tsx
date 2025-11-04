import type { InputProps } from '../types/api';

const Input: React.FC<InputProps> = ({
  id,
  type = 'text',
  placeholder,
  value,
  onChange,
  onKeyPress,
  disabled = false,
  error,
  label,
  className = '',
}) => {
  const baseClasses = 'w-full px-4 py-3 border rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent transition duration-200 ease-in-out disabled:opacity-50 disabled:cursor-not-allowed';
  
  const stateClasses = error 
    ? 'border-red-300 dark:border-red-600 bg-red-50 dark:bg-red-900/20' 
    : 'border-gray-300 dark:border-gray-600 bg-white dark:bg-gray-700';
  
  const textClasses = 'text-gray-900 dark:text-white placeholder-gray-500 dark:placeholder-gray-400';
  
  const classes = `${baseClasses} ${stateClasses} ${textClasses} ${className}`;
  
  return (
    <div className="space-y-2">
      {label && (
        <label htmlFor={id} className="block text-sm font-medium text-gray-700 dark:text-gray-300">
          {label}
        </label>
      )}
      <input
        id={id}
        type={type}
        placeholder={placeholder}
        value={value}
        onChange={(e) => onChange?.(e.target.value)}
        onKeyPress={onKeyPress}
        disabled={disabled}
        className={classes}
      />
      {error && (
        <p className="text-sm text-red-600 dark:text-red-400 mt-1">
          {error}
        </p>
      )}
    </div>
  );
};

export default Input;